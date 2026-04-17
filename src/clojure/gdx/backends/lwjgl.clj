(ns clojure.gdx.backends.lwjgl
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn application!
  [{:keys [application-listener
           config]}]
  (Lwjgl3Application. (let [[f params] application-listener]
                        (f params))
                      (let [[f params] config]
                        (f params))))
