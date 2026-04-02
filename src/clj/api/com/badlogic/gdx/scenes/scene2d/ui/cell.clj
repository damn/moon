(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.cell
  (:import (com.badlogic.gdx.scenes.scene2d.ui Cell)))

(defn bottom! [^Cell cell]
  (.bottom cell))

(defn center! [^Cell cell]
  (.center cell))

(defn right! [^Cell cell]
  (.right cell))

(defn colspan! [^Cell cell col-span]
  (.colspan cell (int col-span)))

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

(defn height! [^Cell cell height]
  (.height cell (float height)))

(defn left! [^Cell cell]
  (.left cell))

(defn pad! [^Cell cell pad]
  (.pad cell (float pad)))

(defn pad-bottom! [^Cell cell pad]
  (.padBottom cell (float pad)))

(defn pad-top! [^Cell cell pad]
  (.padTop cell (float pad)))

(defn width! [^Cell cell width]
  (.width cell (float width)))
