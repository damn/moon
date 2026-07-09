(ns clojure.gdx.graphics.g2d.batch
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]))

(def vertex-indices
  batch/vertex-indices)

(defn begin! [& args]
  (apply batch/begin! args))

(defn end! [& args]
  (apply batch/end! args))

(defn set-color! [& args]
  (apply batch/set-color! args))

(defn get-color [& args]
  (apply batch/get-color args))

(defn set-projection-matrix! [& args]
  (apply batch/set-projection-matrix! args))

(defn draw-vertices! [& args]
  (apply batch/draw-vertices! args))

(defn draw-texture-region! [& args]
  (apply batch/draw-texture-region! args))
