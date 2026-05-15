(ns com.badlogic.gdx.backends.lwjgl3.application
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [{:keys [listener config]}]
  (Lwjgl3Application. (let [[f params] listener]
                        (f params))
                      (let [[f params] config]
                        (f params))))

; PROXY ILOOKJUP AND IMPLEMENT clojure.gdx.app
