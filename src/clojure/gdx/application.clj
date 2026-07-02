(ns clojure.gdx.application
  (:require [clojure.gdx.application-listener.new :as create-listener]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]))

(defn f!
  [{:keys [listener] :as config}]
  (lwjgl3-application/f (create-listener/f
                         (let [[f params] listener]
                           (f params)))
                        (create-config/f config)))
