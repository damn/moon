(ns clojure.gdx.scene2d.actor.create
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create
  [{:keys [act! draw!]}]
  (proxy [Actor] []
    (act [delta]
      (when act!
        (act! this delta))
      (let [^Actor this this]
        (proxy-super act delta)))

    (draw [batch parent-alpha]
      (when draw!
        (draw! this batch parent-alpha)))))
