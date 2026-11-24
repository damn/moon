(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.cell
  "A cell for a [[clojure.com.badlogic.gdx.scenes.scene2d.ui.table]]."
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn bottom!
  "Adds bottom and clears top for the alignment of the actor within the cell."
  [^Cell cell]
  (.bottom cell))

(defn right!
  "Adds right and clears left for the alignment of the actor within the cell."
  [^Cell cell]
  (.right cell))

(defn left!
  "Adds left and clears right for the alignment of the actor within the cell."
  [^Cell cell]
  (.left cell))

(defn center!
  "Sets the alignment of the actor within the cell to [clojure.com.badlogic.gdx.utils.align/center]. This clears any other alignment."
  [^Cell cell]
  (.center cell))

(defn expand!
  "Sets expandX and expandY to 1."
  [^Cell cell]
  (.expand cell))

(defn expand-x!
  "Sets expandX to 1."
  [^Cell cell]
  (.expandX cell))

(defn expand-y!
  "Sets expandY to 1."
  [^Cell cell]
  (.expandY cell))

(defn fill-x!
  "Sets fillX to 1."
  [^Cell cell]
  (.fillX cell))

(defn fill-y!
  "Sets fillY to 1."
  [^Cell cell]
  (.fillY cell))

(defn colspan! [^Cell cell n]
  (.colspan cell (int n)))

(defn pad!
  "Sets the padTop, padLeft, padBottom, and padRight to the specified value."
  [^Cell cell fv]
  (.pad cell (float fv)))

(defn pad-top! [^Cell cell fv]
  (.padTop cell (float fv)))

(defn pad-bottom! [^Cell cell fv]
  (.padBottom cell (float fv)))

(defn width!
  "Sets the minWidth, prefWidth, and maxWidth to the specified value."
  [^Cell cell fv]
  (.width cell (float fv)))

(defn height!
  "Sets the minHeight, prefHeight, and maxHeight to the specified value."
  [^Cell cell fv]
  (.height cell (float fv)))
