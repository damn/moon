(ns clojure.gdx.scenes.scene2d.actor
  (:refer-clojure :exclude [name])
  (:require [clojure.gdx.math.vector2 :as vector2]
            [clojure.gdx.scenes.scene2d.touchable :as touchable]
            [clojure.gdx.utils.align :as align]
            [clojure.scene2d.listener :as listener])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defmulti create :type)

(defprotocol PActor
  (name [_])
  (x [_])
  (y [_])
  (width [_])
  (height [_])
  (user-object [_])
  (stage [_])
  (set-name! [_ name])
  (set-user-object! [_ object])
  (set-position! [_ x y align]
                 [_ [x y]])
  (set-visible! [_ visible?])
  (set-touchable! [_ touchable])
  (visible? [_])
  (hit [_ [x y] touchable?])
  (remove! [_])
  (parent [_])
  (stage->local-coordinates [actor xy])
  (add-listener! [actor [listener-k listener-params]]))

(extend-type Actor
  PActor
  (name [actor]
    (.getName actor))

  (x [actor]
    (.getX actor))

  (y [actor]
    (.getY actor))

  (width [actor]
    (.getWidth actor))

  (height [actor]
    (.getHeight actor))

  (user-object [actor]
    (.getUserObject actor))

  (stage [actor]
    (.getStage actor))

  (set-name! [actor name]
    (.setName actor name))

  (set-user-object! [actor object]
    (.setUserObject actor object))

  (visible? [actor]
    (.isVisible actor))

  (hit [actor [x y] touchable?]
    (.hit actor x y touchable?))

  (remove! [actor]
    (.remove actor))

  (parent [actor]
    (.getParent actor))

  (set-position!
    ([actor [x y]]
     (.setPosition actor x y))
    ([actor x y align]
     (.setPosition actor x y (align/k->value align))))

  (set-visible! [actor visible?]
    (.setVisible actor visible?))

  (set-touchable! [actor touchable]
    (.setTouchable actor (touchable/k->value touchable)))

  (add-listener! [actor [listener-k listener-params]]
    (.addListener actor (listener/create [listener-k listener-params])))

  (stage->local-coordinates [actor xy]
    (vector2/->clj (.stageToLocalCoordinates actor (vector2/->java xy)))))

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

(defmethod create :ui/actor
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
