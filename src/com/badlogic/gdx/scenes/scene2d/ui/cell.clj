(ns com.badlogic.gdx.scenes.scene2d.ui.cell
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn bottom [^Cell cell]
  (.bottom cell))

(defn center [^Cell cell]
  (.center cell))

(defn colspan [^Cell cell n]
  (.colspan cell (int n)))

(defn expand [^Cell cell]
  (.expand cell))

(defn expandX [^Cell cell]
  (.expandX cell))

(defn expandY [^Cell cell]
  (.expandY cell))

(defn fillX [^Cell cell]
  (.fillX cell))

(defn fillY [^Cell cell]
  (.fillY cell))

(defn height [^Cell cell n]
  (.height cell (float n)))

(defn left [^Cell cell]
  (.left cell))

(defn pad [^Cell cell n]
  (.pad cell (float n)))

(defn padBottom [^Cell cell n]
  (.padBottom cell (float n)))

(defn padTop [^Cell cell n]
  (.padTop cell (float n)))

(defn right [^Cell cell]
  (.right cell))

(defn width [^Cell cell n]
  (.width cell (float n)))
