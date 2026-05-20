(ns com.badlogic.gdx.scenes.scene2d.ui.skin
  (:require [gdl.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(extend-type Skin
  skin/Skin
  (font [skin name]
    (.getFont skin name)))
