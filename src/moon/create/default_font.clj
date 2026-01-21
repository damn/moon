(ns moon.create.default-font
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn- generate-font
  [file-handle {:keys [size
                       quality-scaling
                       enable-markup?
                       use-integer-positions?]}]
  (let [generator (FreeTypeFontGenerator. file-handle)
        font (.generateFont generator
                            (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                              (set! (.size params) (* size quality-scaling))
                              (set! (.minFilter params) Texture$TextureFilter/Linear)
                              (set! (.magFilter params) Texture$TextureFilter/Linear)
                              params))]
    (.dispose generator)
    (.setScale (.getData font) (/ quality-scaling))
    (set! (.markupEnabled (.getData font)) enable-markup?)
    (.setUseIntegerPositions font use-integer-positions?)
    font))

(defn do!
  [{:keys [^Files ctx/files]
    :as ctx}
   {:keys [path params]}]
  (assoc ctx :ctx/default-font (generate-font (.internal files path)
                                              params)))
