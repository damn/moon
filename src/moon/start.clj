(ns moon.start
  (:require [clojure.gdx.backends.lwjgl :as lwjgl]
            [moon.application.create :as create]
            [moon.application.dispose :as dispose]
            [moon.application.render :as render]
            [moon.application.resize :as resize])
  (:import (com.badlogic.gdx ApplicationListener))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (lwjgl/use-glfw-async!)
  (lwjgl/application! (reify ApplicationListener
                        (create [_]
                          (reset! state (create/do!)))

                        (dispose [_]
                          (dispose/do! @state))

                        (render [_]
                          (swap! state render/do!))

                        (resize [_ width height]
                          (resize/do! @state width height))

                        (pause [_])

                        (resume [_]))
                      {:title "Moon"
                       :width 1440
                       :height 900
                       :fps 60}))
