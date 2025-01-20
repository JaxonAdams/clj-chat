(ns server.websockets
  (:require [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]))

;; Set up the WebSocket server
(defonce chsk-server
  (sente/make-channel-socket-server!
   (get-sch-adapter)
   {}))

(defonce ch-recv (:ch-recv chsk-server))                ;; Channel Receiver
(defonce send-fn (:send-fn chsk-server))                ;; Send Function
(defonce connected-uids (:connected-uids chsk-server))  ;; Users connected through WebSocket clients

;; Handle incoming messages
(defn handle-incoming-msg [{:keys [id ?data]}]
  (case id
    :chat/send-message
    (let [{:keys [user message]} ?data]
      (println (str user ": " message))
      ;; Broadcast the message to all connected users
      (doseq [uid (:any @connected-uids)] 
        (send-fn uid [:chat/new-message {:user user :message message}])))
    (println "Unhandled message: " id)))

;; Start the WebSocket router
(defn start-router []
  (sente/start-chsk-router! ch-recv handle-incoming-msg))