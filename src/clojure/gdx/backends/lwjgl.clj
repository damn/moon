(ns clojure.gdx.backends.lwjgl
  (:require [com.badlogic.gdx.backends.lwjgl3.application :as application]))

(defn application [{:keys [listener config]}]
  (application/create (let [[f params] listener]
                        (f params))
                      (let [[f params] config]
                        (f params))))
