(ns moon.application
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(def state (atom nil))

(defn apply* [[f params]]
  ((requiring-resolve f) params))

(defn lwjgl-app-config [config]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle (:title config))
    (.setWindowedMode (:width (:window config))
                      (:height (:window config)))
    (.setForegroundFPS (:fps config))))

(defn app-listener
  [{:keys [create!
           dispose!
           render!
           resize!]}]
  (let [create! (requiring-resolve create!)
        dispose! (requiring-resolve dispose!)
        render! (requiring-resolve render!)
        resize! (requiring-resolve resize!)
        config (->> "config.edn" io/resource slurp edn/read-string)]
    (reify ApplicationListener
      (create [_]
        (reset! state (create! Gdx/app config)))

      (dispose [_]
        (dispose! @state))

      (render [_]
        (swap! state render!))

      (resize [_ width height]
        (resize! @state width height))

      (pause [_])

      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (let [{:keys [requires
                listener
                config]} (->> "start.edn"
                              io/resource
                              slurp
                              edn/read-string)]
    (run! require requires)
    (Lwjgl3Application. (apply* listener)
                        (apply* config))))
