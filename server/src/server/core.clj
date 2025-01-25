(ns server.core
  (:gen-class)
  (:require [org.httpkit.server :as http-kit]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [server.websockets :refer [chsk-server start-router]]))

;; Define HTTP routes
(defn app-routes []
  (fn [req]
    (case (:uri req)
      "/chsk" (chsk-server req)
      {:status 404 :body "Not found"})))

;; Wrap the app with middleware to serve static files
(defn wrap-static-files [handler]
  (-> handler
      (wrap-resource "public")  ;; Serve files from client/sersources/public
      wrap-content-type         ;; Add content-type headers
      wrap-not-modified))       ;; Handle conditional GET requests efficiently

;; Combine static file handling and routes
(defn app []
  (wrap-static-files (app-routes)))

;; Start the server
(defn start-server []
  (start-router)
  (http-kit/run-server (app) {:port 3000})
  (println "Server running. Listening for requests on port 3000."))

(defn -main
  "... Engage."
  [& args]
  (start-server))