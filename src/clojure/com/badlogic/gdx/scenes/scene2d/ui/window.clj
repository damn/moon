(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.window
  "A table that can be dragged and act as a modal window. The top padding is used as the window's title height.
  
  The preferred size of a window is the preferred size of the title text and the children as laid out by the table. After adding
  children to the window, it can be convenient to call [clojure.com.badlogic.gdx.scenes.scene2d.utils.layout/pack!] to size the window to the size of the children."
  (:refer-clojure :exclude [class])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin Window)))

(def class Window)

(defn create [^String title ^Skin skin]
  (Window. title skin))

(defn title-table [^Window window]
  (.getTitleTable window))

(defn title-label [^Window window]
  (.getTitleLabel window))

(defn set-modal! [^Window window modal?]
  (.setModal window modal?))

(defn window? [x]
  (instance? Window x))
