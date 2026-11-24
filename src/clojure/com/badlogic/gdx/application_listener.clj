(ns clojure.com.badlogic.gdx.application-listener
  "An application listener is called when the application is created, resumed, rendering, paused or destroyed.
  All methods are called in a thread that has the OpenGL context current. You can thus safely create and manipulate graphics
  resources.
  
  It follows the standard Android activity life-cycle and is emulated on the desktop
  accordingly."
  (:import (com.badlogic.gdx ApplicationListener)))

(defn create [{:keys [create! dispose! render! resize! pause! resume!]}]
  (reify ApplicationListener
    (create [_]
      (create!))
    (dispose [_]
      (dispose!))
    (render [_]
      (render!))
    (resize [_ width height]
      (resize! width height))
    (pause [_]
      (pause!))
    (resume [_]
      (resume!))))
