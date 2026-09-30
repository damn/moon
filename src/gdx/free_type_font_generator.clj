(ns gdx.free-type-font-generator
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn new [file-handle]
  (FreeTypeFontGenerator. ^FileHandle file-handle))

(let [k->opts
      {
       ; TODO convert texture-filter from keyword?
       :set-mag-filter (fn [^FreeTypeFontGenerator$FreeTypeFontParameter parameter ^Texture$TextureFilter filter]
                         (set! (.magFilter parameter) filter))
       :set-min-filter (fn [^FreeTypeFontGenerator$FreeTypeFontParameter parameter ^Texture$TextureFilter filter]
                         (set! (.minFilter parameter) filter))
       :set-size (fn [^FreeTypeFontGenerator$FreeTypeFontParameter parameter size]
                   (set! (.size parameter) size))
       }

      build
      (fn [config-opts]
        (let [config (FreeTypeFontGenerator$FreeTypeFontParameter.)]
          (doseq [[k v] config-opts]
            (let [apply! (k->opts k)]
              (assert apply! (str "Unknown config option: " k))
              (apply! config v)))
          config))]
  (defn generate-font [generator parameter]
    (.generateFont ^FreeTypeFontGenerator generator ^FreeTypeFontGenerator$FreeTypeFontParameter (build parameter))))
