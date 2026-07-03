(ns clojure.gdx.select-box.set-items
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox)))

(defn f [^SelectBox select-box items]
  (SelectBox/.setItems select-box ^"[Ljava.lang.Object;" (into-array items)))
