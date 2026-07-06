(ns clojure.gdx.application
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]))

(defn f!
  [{:keys [listener] :as config}]
  (lwjgl3-application/f (application-listener/new
                         (let [[f params] listener]
                           (f params)))
                        (create-config/f config)))
