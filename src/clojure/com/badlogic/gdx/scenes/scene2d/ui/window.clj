(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin Window)))

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
