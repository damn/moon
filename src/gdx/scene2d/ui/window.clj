(ns gdx.scene2d.ui.window
  (:refer-clojure :exclude [class])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(def class Window)

(defn create [title skin]
  (Window. ^String title ^Skin skin))

(defn set-modal! [^Window window]
  (.setModal window true))

(defn title-table [^Window window]
  (.getTitleTable window))

(defn title-label [^Window window]
  (.getTitleLabel window))
