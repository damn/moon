(ns clojure.application-listener
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx ApplicationListener)))

(defn new
  [{:keys [create!
           dispose!
           render!
           resize!
           pause!
           resume!]}]
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
