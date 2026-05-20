(ns gdl.scene2d.actor
  (:refer-clojure :exclude [name]))

; ** TODO this contains '2' things !!!
(defmulti create :type)
; 91 use cases !!
; mostly w. table

(defprotocol Actor
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
