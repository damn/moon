(ns com.badlogic.gdx.scenes.scene2d.ui.window
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin Window)))

(defn get-title-label [^Window window]
  (Window/.getTitleLabel window))

(defn get-title-table [^Window window]
  (Window/.getTitleTable window))

(defn instance? [x]
  (instance? Window x))

(defn new [^String title ^Skin skin]
  (Window. title skin))

(defn set-modal! [^Window window modal?]
  (Window/.setModal window modal?))
