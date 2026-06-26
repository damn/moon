(ns com.badlogic.gdx.scenes.scene2d.ui.scroll-pane
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ScrollPane)))

(defn create [actor skin]
  (ScrollPane. (actor/type-hint actor) (skin/type-hint skin)))
