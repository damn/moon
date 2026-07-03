(ns clojure.gdx.select-box.set-selected
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox)))

(defn f [^SelectBox select-box selected]
  (SelectBox/.setSelected select-box selected))
