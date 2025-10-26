(ns moon.ui.stage
  (:require [moon.scene2d.stage :as stage])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (moon.ui Stage)))

(defn create [viewport batch config]
  (Stage. viewport batch config))

(defn ctx [^Stage stage]
  (.ctx stage))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))

(defn act! [stage]
  (stage/act! stage))

(defn draw! [stage]
  (stage/draw! stage))

(defmulti build :type)

#_(defn- build? [actor-or-decl]
  (if (instance? Actor actor-or-decl)
    actor-or-decl
    (build actor-or-decl)))

(defn add-actor! [stage actor-decl]
  (stage/add-actor! stage (build actor-decl)))

(defn viewport [stage]
  (stage/viewport stage))

(defn root [stage]
  (stage/root stage))

(defn hit [stage position touchable?]
  (stage/hit stage position touchable?))

(defn clear! [stage]
  (stage/clear! stage))
