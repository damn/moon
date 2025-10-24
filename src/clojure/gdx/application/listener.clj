(ns clojure.gdx.application.listener
  (:import (com.badlogic.gdx ApplicationListener)))

(defn create
  [{:keys [create dispose render resize pause resume]}]
  (reify ApplicationListener
    (create [_]
      (create))
    (dispose [_]
      (dispose))
    (render [_]
      (render))
    (resize [_ width height]
      (resize width height))
    (pause [_]
      (pause))
    (resume [_]
      (resume))))
