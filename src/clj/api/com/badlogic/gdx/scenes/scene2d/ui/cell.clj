(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.cell
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn right! [^Cell cell]
  (.right cell))

(defn expand-x! [^Cell cell]
  (.expandX cell))
