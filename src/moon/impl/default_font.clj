(ns moon.impl.default-font
  (:require [clojure.files :as files]
            [clojure.gdx :as gdx]
            [clojure.graphics.bitmap-font :as bitmap-font]
            [clojure.graphics.freetype :as freetype]))

(defn create [{:keys [ctx/files]} {:keys [path params]}]
  (let [{:keys [size
                quality-scaling
                enable-markup?
                use-integer-positions?]} params]
    (doto (freetype/generate-font (gdx/app)
                                  (files/internal files path)
                                  {:size (* size quality-scaling)})
      (bitmap-font/set-scale! (/ quality-scaling))
      (bitmap-font/enable-markup! enable-markup?)
      (bitmap-font/use-integer-positions! use-integer-positions?))))
