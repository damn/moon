(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.cell
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn bottom! [^Cell cell]
  (.bottom cell))

(defn right! [^Cell cell]
  (.right cell))

(defn left! [^Cell cell]
  (.left cell))

(defn center! [^Cell cell]
  (.center cell))

(defn expand! [^Cell cell]
  (.expand cell))

(defn expand-x! [^Cell cell]
  (.expandX cell))

(defn expand-y! [^Cell cell]
  (.expandY cell))

(defn fill-x! [^Cell cell]
  (.fillX cell))

(defn fill-y! [^Cell cell]
  (.fillY cell))

(defn colspan! [^Cell cell n]
  (.colspan cell (int n)))

(defn pad! [^Cell cell fv]
  (.pad cell (float fv)))

(defn pad-top! [^Cell cell fv]
  (.padTop cell (float fv)))

(defn pad-bottom! [^Cell cell fv]
  (.padBottom cell (float fv)))

(defn width! [^Cell cell fv]
  (.width cell (float fv)))

(defn height! [^Cell cell fv]
  (.height cell (float fv)))
