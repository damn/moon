(ns clojure.gdx.backends.lwjgl
  (:require [clj.api.com.badlogic.gdx.backends.lwjgl3.application :as application]))

(defn application!
  [{:keys [application-listener
           config]}]
  (application/create (let [[f params] application-listener]
                        (f params))
                      (let [[f params] config]
                        (f params))))
