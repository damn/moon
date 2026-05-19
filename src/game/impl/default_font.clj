(ns game.impl.default-font
  (:require [gdl.app :as app]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.graphics.g2d.bitmap-font :as font]
            [gdl.graphics.g2d.bitmap-font.data :as font.data]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator]))

(def path "exocet/films.EXL_____.ttf")
(def size 16)
(def quality-scaling 2)

(defn create
  [{:keys [ctx/app]}]
  (let [generator (file-handle/freetype-font-generator (files/internal (app/files app) path))
        font (font-generator/generate-font generator {:size (* size quality-scaling)})]
    (font-generator/dispose! generator)
    (font.data/set-scale! (font/data font) (/ quality-scaling))
    (font.data/set-markup-enabled! (font/data font) true)
    (font/use-integer-positions! font false)
    font))
