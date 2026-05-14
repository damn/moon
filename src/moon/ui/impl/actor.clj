(ns moon.ui.impl.actor
  (:refer-clojure :exclude [name])
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [com.badlogic.gdx.utils.align :as align])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener
                                                  ClickListener)))

(defn name [^Actor actor]
  (.getName actor))

(defn x [^Actor actor]
  (.getX actor))

(defn y [^Actor actor]
  (.getY actor))

(defn width [^Actor actor]
  (.getWidth actor))

(defn height [^Actor actor]
  (.getHeight actor))

(defn user-object [^Actor actor]
  (.getUserObject actor))

(defn stage [^Actor actor]
  (.getStage actor))

(defn set-name! [^Actor actor name]
  (.setName actor name))

(defn set-user-object! [^Actor actor object]
  (.setUserObject actor object))

(defn set-position!
  ([^Actor actor [x y]]
   (.setPosition actor x y))
  ([^Actor actor x y align]
   (.setPosition actor x y (align/k->value align))))

(defn set-visible! [^Actor actor visible?]
  (.setVisible actor visible?))

(defn set-touchable! [^Actor actor touchable]
  (.setTouchable actor (touchable/k->value touchable)))

(defn visible? [^Actor actor]
  (.isVisible actor))

(defn hit [^Actor actor [x y] touchable?]
  (.hit actor x y touchable?))

(defn remove! [^Actor actor]
  (.remove actor))

(defn parent [^Actor actor]
  (.getParent actor))

(defn add-listener! [^Actor actor [listener-k listener-params]]
  (.addListener actor
                (case listener-k
                  :listener/change (let [f listener-params]
                                     (proxy [ChangeListener] []
                                       (changed [event actor]
                                         (f event actor))))
                  :listener/text-tooltip (let [[tooltip skin] listener-params]
                                           (TextTooltip. ^String tooltip ^Skin skin))
                  :listener/click (let [f listener-params]
                                    (proxy [ClickListener] []
                                      (clicked [event x y]
                                        (f event x y)))))))

(defn stage->local-coordinates [^Actor actor xy]
  (vector2/->clj (.stageToLocalCoordinates actor (vector2/->java xy))))

(defn find-ancestor [actor pred]
  (if-let [p (parent actor)]
    (if (pred p)
      p
      (find-ancestor p pred))
    (throw (Error. (str "Actor has no parent window " actor)))))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))

(defn set-opts! [actor opts]
  (when-let [user-object (:actor/user-object opts)]
    (set-user-object! actor user-object))

  (when (:actor/position opts)
    (let [[x y align] (:actor/position opts)]
      (if align
        (set-position! actor x y align)
        (set-position! actor [x y]))))

  (when (contains? opts :actor/visible?)
    (set-visible! actor (:actor/visible? opts)))

  (when-let [touchable (:actor/touchable opts)]
    (set-touchable! actor touchable))

  (when-let [name (:actor/name opts)]
    (set-name! actor name))

  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (add-listener! actor listener))))

(defn create
  [{:keys [act! draw!] :as opts}]
  (doto (proxy [Actor] []
          (act [delta]
            (when act!
              (act! this delta))
            (let [^Actor this this]
              (proxy-super act delta)))
          (draw [batch parent-alpha]
            (when draw!
              (draw! this batch parent-alpha))))
    (set-opts! opts)))
