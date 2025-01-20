(ns server.core
  (:gen-class)
  (:require [org.httpkit.server :as http-kit]
            [server.websockets :refer [chsk-server start-router]]))

;; Define HTTP routes
(defn app-routes []
  (fn [req]
    (case (:uri req)
      "/chsk" (chsk-server req)
      {:status 404 :body "Not found"})))

;; Start the server
(defn start-server []
  (start-router)
  (http-kit/run-server (app-routes) {:port 3000}))

(defn -main
  "... Engage."
  [& args]
  (println "Starting server...")
  (start-server))