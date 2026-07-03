(ns clojure.gdx.actor.new
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [act! draw!]
  (proxy [Actor] []
    (act [delta]
      (act! this delta)
      (let [^Actor this this]
        (proxy-super act delta)))

    (draw [batch parent-alpha]
      (draw! this batch parent-alpha))))
