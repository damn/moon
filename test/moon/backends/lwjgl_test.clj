(ns moon.backends.lwjgl-test
  (:require [moon.backends.lwjgl :as lwjgl])
  (:import (com.badlogic.gdx ApplicationListener)))

(defn -main []
  (lwjgl/use-glfw-async!)
  (lwjgl/application! (reify ApplicationListener
                        (create [_])
                        (dispose [_])
                        (render [_])
                        (resize [_ width height])
                        (pause [_])
                        (resume [_]))
                      {:title "foobar"
                       :width 800
                       :height 600
                       :fps 60}))
