(ns moon.application
  (:require [moon.application.create :as create]
            [moon.application.dispose :as dispose]
            [moon.application.render :as render]
            [moon.application.resize :as resize])
  (:import (com.badlogic.gdx ApplicationListener)))

(def state (atom nil))

(def listener
  (reify ApplicationListener
    (create [_]
      (reset! state (create/do!)))

    (dispose [_]
      (dispose/do! @state))

    (render [_]
      (swap! state render/do!))

    (resize [_ width height]
      (resize/do! @state width height))

    (pause [_])

    (resume [_])))
