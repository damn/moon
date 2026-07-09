(ns clojure.gdx.graphics.g2d.batch
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]))

(def X1 batch/X1)
(def Y1 batch/Y1)
(def C1 batch/C1)
(def U1 batch/U1)
(def V1 batch/V1)
(def X2 batch/X2)
(def Y2 batch/Y2)
(def C2 batch/C2)
(def U2 batch/U2)
(def V2 batch/V2)
(def X3 batch/X3)
(def Y3 batch/Y3)
(def C3 batch/C3)
(def U3 batch/U3)
(def V3 batch/V3)
(def X4 batch/X4)
(def Y4 batch/Y4)
(def C4 batch/C4)
(def U4 batch/U4)
(def V4 batch/V4)

(defn begin! [& args]
  (apply batch/begin args))

(defn end! [& args]
  (apply batch/end args))

(defn set-color! [& args]
  (apply batch/setColor args))

(defn get-color [& args]
  (apply batch/getColor args))

(defn set-projection-matrix! [& args]
  (apply batch/setProjectionMatrix args))

(defn draw! [& args]
  (apply batch/draw args))
