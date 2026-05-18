(ns com.badlogic.gdx.scenes.scene2d.ctx-stage
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx.scenes.scene2d CtxStage)))

(defn create [viewport batch]
  (proxy [CtxStage ILookup] [viewport batch]
    (valAt [k]
      (case k
        ; TODO :stage/root
        :stage/ctx      (.ctx         ^CtxStage this)
        :stage/viewport (.getViewport ^CtxStage this)))))

(defn set-ctx! [^CtxStage stage ctx]
  (set! (.ctx stage) ctx))

(defn add-actor! [^CtxStage stage actor]
  (.addActor stage actor))

(defn act! [^CtxStage stage]
  (.act stage))

(defn draw! [^CtxStage stage]
  (.draw stage))

(defn root [^CtxStage stage]
  (.getRoot stage))

(defn hit [^CtxStage stage [x y] touchable]
  (.hit stage x y true))
