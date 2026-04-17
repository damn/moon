(ns moon.create.default-font
  (:require [clojure.graphics.freetype :as freetype]
            [clojure.graphics.bitmap-font :as font]
            [clojure.files :as files]))

(defn do!
  [{:keys [ctx/app
           ctx/files]
    :as ctx}
   {:keys [path params]}]
  (assoc ctx :ctx/default-font
         (let [{:keys [size
                       quality-scaling
                       enable-markup?
                       use-integer-positions?]} params]
           (doto (freetype/generate-font app
                                         (files/internal files path)
                                         {:size (* size quality-scaling)})
             (font/set-scale! (/ quality-scaling))
             (font/enable-markup! enable-markup?)
             (font/use-integer-positions! use-integer-positions?)))))
