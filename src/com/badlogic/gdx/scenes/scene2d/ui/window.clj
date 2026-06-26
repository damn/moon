(ns com.badlogic.gdx.scenes.scene2d.ui.window
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(def java-class Window)

(defn create [title skin]
  (Window. ^String title (skin/type-hint skin)))

(defn set-modal! [^Window window modal?]
  (.setModal window modal?))

(defn get-title-table [^Window window]
  (.getTitleTable window))

(defn get-title-label [^Window window]
  (.getTitleLabel window))
