(ns clojure.gdx.application
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-application-configuration]))

(defn f!
  [{:keys [listener] :as config}]
  (lwjgl3-application/new (application-listener/new
                         (let [[f params] listener]
                           (f params)))
                        (lwjgl3-application-configuration/new config)))
