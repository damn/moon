(ns com.badlogic.gdx.application-listener
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)))

(defn application-listener
  [{:keys [create!
           dispose!
           render!
           resize!
           pause!
           resume!]}]
  (reify ApplicationListener
    (create [_]
      (create! Gdx/app))

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
