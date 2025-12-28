(ns clojure.gdx
  (:import (com.badlogic.gdx Gdx)))

(defn context []
  {:ctx/audio    Gdx/audio
   :ctx/files    Gdx/files
   :ctx/graphics Gdx/graphics
   :ctx/input    Gdx/input})
