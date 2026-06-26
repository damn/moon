(ns com.badlogic.gdx.scenes.scene2d.ui.cell
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn fill-x? [^Cell cell]
  (.fillX cell))

(defn fill-y? [^Cell cell]
  (.fillY cell))

(defn expand [^Cell cell]
  (.expand cell))

(defn expand-x [^Cell cell]
  (.expandX cell))

(defn expand-y [^Cell cell]
  (.expandY cell))

(defn bottom [^Cell cell]
  (.bottom cell))

(defn colspan! [^Cell cell n]
  (.colspan cell (int n)))

(defn pad! [^Cell cell n]
  (.pad cell (float n)))

(defn pad-top! [^Cell cell n]
  (.padTop cell (float n)))

(defn pad-bottom! [^Cell cell n]
  (.padBottom cell (float n)))

(defn width! [^Cell cell n]
  (.width cell (float n)))

(defn height! [^Cell cell n]
  (.height cell (float n)))

(defn center [^Cell cell]
  (.center cell))

(defn right [^Cell cell]
  (.right cell))

(defn left [^Cell cell]
  (.left cell))

(defn type-hint
  ^Cell
  [obj]
  obj)
