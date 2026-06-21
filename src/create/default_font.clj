(ns create.default-font
  (:require [com.badlogic.gdx.files :as files]
            [gdx.freetype :refer [generate-font]]))

(defn step
  [{:keys [ctx/files]}]
  (generate-font (files/internal files "fonts/films.EXL_____.ttf")
                 {:size 16
                  :quality-scaling 2
                  :use-integer-positions? false}))
