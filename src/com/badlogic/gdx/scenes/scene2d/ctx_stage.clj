(ns com.badlogic.gdx.scenes.scene2d.ctx-stage
  (:import (com.badlogic.gdx.scenes.scene2d CtxStage)))

(defn create [viewport batch]
  (CtxStage. viewport batch))

(defn ctx [^CtxStage stage]
  (.ctx stage))

(defn set-ctx! [^CtxStage stage ctx]
  (set! (.ctx stage) ctx))

(defn add-actor! [^CtxStage stage actor]
  (.addActor stage actor))

(defn root [^CtxStage stage]
  (.getRoot stage))

(defn viewport [^CtxStage stage]
  (.getViewport stage))

(defn act! [^CtxStage stage]
  (.act stage))

(defn draw! [^CtxStage stage]
  (.draw stage))

(defn hit [^CtxStage stage [x y] touchable?]
  (.hit stage x y touchable?))
