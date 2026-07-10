(ns com.badlogic.gdx.scenes.scene2d.ui.window
  (:refer-clojure :exclude [new class])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin Window)))

(def class Window)

(defn new [^String title ^Skin skin]
  (Window. title skin))

(defn getTitleLabel [^Window window]
  (.getTitleLabel window))

(defn getTitleTable [^Window window]
  (.getTitleTable window))

(defn setModal [^Window window modal?]
  (.setModal window modal?))
