(ns moon.application.create.default-font
  (:require [clojure.graphics.bitmap-font :as bitmap-font]
            [clojure.graphics.freetype :as freetype])
  (:import (com.badlogic.gdx Files)))

(defn step
  [{:keys [ctx/files]
    :as ctx}]
  (assoc ctx :ctx/default-font (let [{:keys [path
                                             size
                                             quality-scaling
                                             enable-markup?
                                             use-integer-positions?]} {
                                                                       :path "exocet/films.EXL_____.ttf"
                                                                       :size 16
                                                                       :quality-scaling 2
                                                                       :enable-markup? true
                                                                       :use-integer-positions? false
                                                                       ; :texture-filter/linear because scaling to world-units
                                                                       :min-filter :linear
                                                                       :mag-filter :linear
                                                                       }]
                                 (doto (freetype/generate-font (Files/.internal files path)
                                                               {:size (* size quality-scaling)})
                                   (bitmap-font/set-scale! (/ quality-scaling))
                                   (bitmap-font/enable-markup! enable-markup?)
                                   (bitmap-font/use-integer-positions! use-integer-positions?)))))
