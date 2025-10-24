(ns cdq.application
  (:require [clojure.gdx.graphics.color :as color]
            [clojure.gdx.graphics.colors :as colors]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [moon.app :as app]
            [moon.core :refer [edn-resource]])
  (:import (com.badlogic.gdx Application
                             ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [config (edn-resource "config.edn")
        create!  (:create!  config)
        dispose! (:dispose! config)
        render!  (:render!  config)
        resize!  (:resize!  config)]
    (Lwjgl3ApplicationConfiguration/useGlfwAsync)
    (Lwjgl3Application. (reify ApplicationListener
                          (create [_]
                            (reset! state (create! {:ctx/app Gdx/app} config)))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (swap! state (fn [ctx]
                                           (reduce (fn [ctx f]
                                                     (f ctx))
                                                   ctx
                                                   render!))))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_]))
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle (:title config))
                          (.setWindowedMode (:width (:window config))
                                            (:height (:window config)))
                          (.setForegroundFPS (:fps config))))))

(extend-type Application
  app/Input
  (set-input-processor! [this input-processor]
    (.setInputProcessor (.getInput this) input-processor))

  app/Graphics
  (def-color! [_ name rgba]
    (colors/put! name (color/create rgba)))

  (sprite-batch [_]
    (sprite-batch/create)))
