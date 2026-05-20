(ns com.badlogic.gdx.application-listener
  (:require [gdl.application-listener :as listener])
  (:import (com.badlogic.gdx ApplicationListener)))

(defn create [listener]
  (reify ApplicationListener
    (create [_]
      (listener/create! listener com.badlogic.gdx.Gdx/app))

    (dispose [_]
      (listener/dispose! listener))

    (render [_]
      (listener/render! listener))

    (resize [_ width height]
      (listener/resize! listener width height))

    (pause [_]
      (listener/pause! listener))

    (resume [_]
      (listener/resume! listener))))
