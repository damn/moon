(ns ctx.default-font
  (:require [clojure.gdx :as gdx]))

(defn step
  [{:keys [ctx/files]}]
  (let [{:keys [path
                size
                quality-scaling
                use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                          :size 16
                                          :quality-scaling 2
                                          :use-integer-positions? false}
        generator (gdx/freetype-font-generator (gdx/internal files path))
        parameter (gdx/freetype-font-parameter)
        _ (gdx/freetype-font-parameter-set-size! parameter (* size quality-scaling))
        _ (gdx/freetype-font-parameter-set-min-filter! parameter gdx/texture-filter-linear)
        _ (gdx/freetype-font-parameter-set-mag-filter! parameter gdx/texture-filter-linear)
        font (gdx/freetype-font-generator-generate-font generator parameter)]
    (gdx/freetype-font-generator-dispose! generator)
    (gdx/font-set-scale! font (/ quality-scaling))
    (gdx/font-set-markup-enabled! font true)
    (gdx/font-set-use-integer-positions! font use-integer-positions?)
    font))
