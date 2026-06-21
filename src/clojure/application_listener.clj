(ns clojure.application-listener
  (:import (com.badlogic.gdx ApplicationListener)))

(defn application-listener
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
