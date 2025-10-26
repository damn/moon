(ns cdq.application
  (:require [moon.graphics.color :as color]
            [moon.graphics.colors :as colors]
            [moon.graphics.g2d.sprite-batch :as sprite-batch]
            [moon.app :as app]
            [moon.core :refer [call edn-resource]])
  (:import (com.badlogic.gdx Application
                             ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(def state (atom nil))

(defn start! [config]
  (let [create!  (:create!  config)
        dispose! (:dispose! config)
        render!  (:render!  config)
        resize!  (:resize!  config)
        listener (reify ApplicationListener
                   (create [_]
                     (reset! state (reduce (fn [ctx [f & params]]
                                             (apply f ctx params))
                                           {:ctx/app Gdx/app}
                                           create!)))

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
        app-config (doto (Lwjgl3ApplicationConfiguration.)
                     (.setTitle (:title config))
                     (.setWindowedMode (:width (:window config))
                                       (:height (:window config)))
                     (.setForegroundFPS (:fps config)))]
    (Lwjgl3Application. listener app-config)))

(defn -main []
  (run! call (edn-resource "config.edn")))

(extend-type Application
  app/Input
  (set-input-processor! [this input-processor]
    (.setInputProcessor (.getInput this) input-processor))

  app/Graphics
  (def-color! [_ name rgba]
    (colors/put! name (color/create rgba)))

  (sprite-batch [_]
    (sprite-batch/create)))
