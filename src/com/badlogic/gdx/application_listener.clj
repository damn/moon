(ns com.badlogic.gdx.application-listener
  (:import (com.badlogic.gdx ApplicationListener)))

(defn create
  [{:keys [create! dispose! render! resize!]}]
  (reify ApplicationListener
    (create [_]
      (create!))

    (dispose [_]
      (dispose!))

    (render [_]
      (render!))

    (resize [_ width height]
      (resize! width height))

    (pause [_])

    (resume [_])))
